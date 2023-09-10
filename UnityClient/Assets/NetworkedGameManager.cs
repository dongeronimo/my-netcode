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
using UniRx;

public class NetworkedGameManager : MonoBehaviour, IObserver<GameState>
{
    private IncomingDatagramHandller incomingDatagramHandller;
    [Header("Connection Properties")]
    [SerializeField]
    private string hostname;
    [SerializeField]
    private int port;
    [SerializeField]
    private string httpHost;
    [SerializeField]
    private bool isConnected;

    public bool IsConnected() { return isConnected; }
    private UdpClient udpClient;
    public string tokem;


    private void Start()
    {
        GameStateStorage.state.SubscribeOnMainThread().Subscribe(this);
    }
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
        //Udp "connection"
        GameStateStorage.Connecting();
        IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
        IPAddress hostAddress = hostDnsEntries.AddressList[0];
        udpClient = new UdpClient();
        udpClient.Connect(hostname: hostname, port: port);
        //Listener thread
        if (incomingDatagramHandller == null) 
            incomingDatagramHandller = new IncomingDatagramHandller(hostAddress, port, udpClient);
        incomingDatagramHandller.Start();
        //Sends the connection message
        SendPacket(tokem, "HELLO",  port, udpClient);
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

    private void SendPacket(string token, string payload, int port, UdpClient udpClient)
    {
        var Timestamp = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
        string message = $"{Timestamp}###{token}###{payload}";
        udpClient.Connect(hostname: hostname, port: port);
        byte[] sendBytes = Encoding.ASCII.GetBytes(message);
        udpClient.Send(sendBytes, sendBytes.Length);
    }

    void IObserver<GameState>.OnCompleted()
    {
        throw new NotImplementedException("O observer deveria ser eterno e nunca completar");
    }

    void IObserver<GameState>.OnError(Exception error)
    {
        throw new NotImplementedException("O observer nunca deveria jogar erro");
    }
    private GameState previousState = new GameState { connectionState = ConnectionState.NotConnected };

    void IObserver<GameState>.OnNext(GameState newState)
    {
        Debug.Log($"state = connectionState={newState.connectionState}");
        UpdateIsConnectedFlag(newState);
        previousState = newState;
    }

    private void UpdateIsConnectedFlag(GameState newState)
    {
        if (newState.connectionState == ConnectionState.Connected)
        {
            isConnected = true;
        }
        else
        {
            isConnected = false;
        }
    }
}
