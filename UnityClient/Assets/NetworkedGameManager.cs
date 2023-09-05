using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Net;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;
using System.Threading;

public class NetworkedGameManager : MonoBehaviour
{
    [Header("Connection Properties")]
    [SerializeField]
    private string hostname;
    [SerializeField]
    private int port;
    [SerializeField]
    private string httpHost;

    public string tokem;
    public void LoginWithAlice()
    {
        StartCoroutine(Login((token) => this.tokem = token, "alice", "blablabla"));
    }
    public void LoginWithBob()
    {
        StartCoroutine(Login((token) => this.tokem = token, "bob", "blablabla"));
    }

    public void Connect()
    {
        //Send a hello packet to the server so that the server stores my connection data
        string payload = "HELLO";
        SendPacket(tokem, payload, false);
    }
    


    IEnumerator Login(System.Action<string> afterLogin, string username, string password)
    {
        AccountCredentials credentials = new AccountCredentials() { password = password, username = username };
        var request = new UnityWebRequest($"{httpHost}/login", "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(credentials));
        request.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");
        yield return request.SendWebRequest();
        var responseHeaders = request.GetResponseHeaders();
        var token = responseHeaders["Authorization"];
        afterLogin(token);
    }

    private void SendPacket(string token, string payload, bool waitAnswer = true)
    {
        IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
        var hostAddress = hostDnsEntries.AddressList[0];
        UdpClient client = new UdpClient();

        var Timestamp = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
        string message = $"{Timestamp}###{token}###{payload}";
        client.Connect(hostname: hostname, port: port);
        byte[] sendBytes = Encoding.ASCII.GetBytes(message);
        client.Send(sendBytes, sendBytes.Length);
        if (waitAnswer)
        {
            IPEndPoint ep = new IPEndPoint(hostAddress, port);
            byte[] incomingBytes = client.Receive(ref ep);
            string incomingData = Encoding.ASCII.GetString(incomingBytes);
            string[] pieces = incomingData.Split("###");
            Debug.Log($"{pieces[0]}:{pieces[1]}");
        }
    }
}
