    using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;
using UnityEngine.UI;

public class IncomingDatagramHandller
{
    private bool threadFlag = true;
    private Thread thread;
    private IPAddress hostAddress;
    private int port;
    private UdpClient udpClient;
    public IncomingDatagramHandller(IPAddress hostAddress, int port, UdpClient udpClient)
    {
        this.hostAddress = hostAddress;
        this.port = port;   
        this.udpClient = udpClient;
    }
    /// <summary>
    /// Lauches the thread.
    /// </summary>
    public void Start()
    {
        Debug.Log("Beginning listener thread");
        threadFlag = true;
        IPEndPoint ep = new IPEndPoint(hostAddress, port);
        byte[] incomingBytes = new byte[1400];
        thread = new Thread(() =>
        {
            while (threadFlag)
            {
                //get the datagram
                byte[] incomingBytes = udpClient.Receive(ref ep);
                string incomingData = Encoding.ASCII.GetString(incomingBytes).Trim();
                string[] pieces = incomingData.Split("###");
                Debug.Log($"{pieces[0]}; {pieces[1]}; {pieces[2]}");
                
            }
        });
        thread.Start();
    }
    /// <summary>
    /// Stops the thread. Coroutine wil yield until the thread is dead.
    /// </summary>
    /// <returns></returns>
    public IEnumerator Halt()
    {
        threadFlag = false;
        thread.Join();
        yield return new WaitWhile(() => thread.IsAlive);
    }
}
