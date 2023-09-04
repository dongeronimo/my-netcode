using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using UnityEngine;

public class HelloWorldUDP : MonoBehaviour
{
    [SerializeField]
    private string hostname;
    [SerializeField]
    private int port;
    // Start is called before the first frame update
    void Start()
    {
        IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
        var hostAddress = hostDnsEntries.AddressList[0];
        UdpClient client = new UdpClient();
        try
        {
           
            client.Connect(hostname: hostname, port: port);
            byte[] sendBytes = Encoding.ASCII.GetBytes("Hello World");
            client.Send(sendBytes, sendBytes.Length);
            //IPEndPoint ep = new IPEndPoint(hostAddress, port);
            //byte[] receivedBytes = client.Receive(ref ep);
            //string returnData = Encoding.ASCII.GetString(receivedBytes);
            //Debug.Log(returnData);
        }
        catch(System.Exception ex)
        {
            Debug.LogException(ex);
        }
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
