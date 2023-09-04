using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net.Sockets;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApp
{
    public class HelloWorldUDP
    {
        private string hostname;
        private int port;
        public HelloWorldUDP(string hostname, int port)
        {
            this.hostname = hostname;
            this.port = port;
        }
        public void DoIt()
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
            catch (System.Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }
    }
}
