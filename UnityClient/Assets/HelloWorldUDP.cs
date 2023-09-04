using Newtonsoft.Json;
using System.Collections;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System;

public class HelloWorldUDP : MonoBehaviour
{
    [SerializeField]
    private string hostname;
    [SerializeField]
    private int port;
    [SerializeField]
    private string username;
    [SerializeField]
    private string password;
    [SerializeField]
    private string httpHost;
    IEnumerator login(System.Action<string> afterLogin)
    {
        AccountCredentials credentials = new AccountCredentials() { password = password, username = username };
        var request = new UnityWebRequest($"{httpHost}/login", "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(credentials));
        request.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");
        yield return request.SendWebRequest();
        Debug.Log("Status Code: " + request.responseCode);
        var responseHeaders = request.GetResponseHeaders();
        var token = responseHeaders["Authorization"];
        afterLogin(token);
    }
    private void SendPacket(string token, string payload)
    {
        IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
        var hostAddress = hostDnsEntries.AddressList[0];
        UdpClient client = new UdpClient();

        var Timestamp = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
        string message = $"{Timestamp}###{token}###{payload}";
        client.Connect(hostname: hostname, port: port);
        byte[] sendBytes = Encoding.ASCII.GetBytes(message);
        client.Send(sendBytes, sendBytes.Length);

        IPEndPoint ep = new IPEndPoint(hostAddress, port);
        byte[] incomingBytes = client.Receive(ref ep);
        string incomingData = Encoding.ASCII.GetString(incomingBytes);
        string[] pieces = incomingData.Split("###");
        Debug.Log($"{pieces[0]}:{pieces[1]}");
        //byte[] receivedBytes = client.Receive(ref ep);
        //string returnData = Encoding.ASCII.GetString(receivedBytes);
        //Debug.Log(returnData);

    }
    IEnumerator SendData(string token)
    {
        while (true)
        {
            SendPacket(token, "TODO: Send inputs");
            yield return new WaitForEndOfFrame();
        }
    }

    // Start is called before the first frame update
    void Start()
    {
        StartCoroutine(login(token =>
        {
            Debug.Log($"token = {token}");
            StartCoroutine(SendData(token));
        }));
        //IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
        //var hostAddress = hostDnsEntries.AddressList[0];
        //UdpClient client = new UdpClient();
        //try
        //{

        //    client.Connect(hostname: hostname, port: port);
        //    byte[] sendBytes = Encoding.ASCII.GetBytes("Hello World");
        //    client.Send(sendBytes, sendBytes.Length);
        //    //IPEndPoint ep = new IPEndPoint(hostAddress, port);
        //    //byte[] receivedBytes = client.Receive(ref ep);
        //    //string returnData = Encoding.ASCII.GetString(receivedBytes);
        //    //Debug.Log(returnData);
        //}
        //catch(System.Exception ex)
        //{
        //    Debug.LogException(ex);
        //}
    }

    // Update is called once per frame
    void Update()
    {

    }
}
