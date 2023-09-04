using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Linq;
using System.Net.Sockets;
using System.Net;
using System.Security.Cryptography;

namespace ConsoleApp
{
    public class JwtSentToUdp
    {
        private string hostname;
        private int port;
        private string username;
        private string password;

        public JwtSentToUdp(string hostname, int port, string username, string password)
        {
            this.hostname = hostname;
            this.port = port;
            this.username = username;
            this.password = password;
        }


        public void DoIt()
        {
            string jwt = logIn(username, password);
            string message = "loren ipsun dolor sit amet";
            sendUDP(jwt, message);
        }

        private void sendUDP(string jwt, string body) {
            IPHostEntry hostDnsEntries = Dns.GetHostEntry(hostNameOrAddress: hostname);
            var hostAddress = hostDnsEntries.AddressList[0];
            UdpClient client = new UdpClient();
            try
            {

                client.Connect(hostname: hostname, port: port);
                string message = $"{jwt} body {body}";
                byte[] sendBytes = Encoding.ASCII.GetBytes(message);
                client.Send(sendBytes, sendBytes.Length);
            }
            catch (System.Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }
        private string logIn(string username, string password)
        {
            var client = new HttpClient();
            using StringContent jsonContent = new(JsonSerializer.Serialize(new
            {
                username = username,
                password = password
            }), Encoding.UTF8, "application/json");
            var request = new HttpRequestMessage()
            {
                RequestUri = new Uri("http://localhost:8080/login"),
                Method = HttpMethod.Post,
                Content = jsonContent
            };
            request.Headers.Add("content", "application/json");
            HttpResponseMessage response = client.Send(request);
            string jwt = response.EnsureSuccessStatusCode().Headers.GetValues("authorization").First();
            return jwt;
        }
    }
}
