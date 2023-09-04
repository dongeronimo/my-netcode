using ConsoleApp;
using System;

namespace MyApp // Note: actual namespace depends on the project name.
{
    internal class Program
    {
        /// <summary>
        /// How to send something to the udp endpoint?
        /// </summary>
        private static void HelloUDP()
        {
            var hello = new HelloWorldUDP("localhost", 11111);
            hello.DoIt();
        }
        /// <summary>
        /// How to get the jwt and send the udp with the jwt?
        /// </summary>
        private static void JwtSentToUdp()
        {
            var x = new JwtSentToUdp("localhost", 11111, "alice", "blablabla");
            x.DoIt();
        }
        static void Main(string[] args)
        {
            //HelloUDP();
            JwtSentToUdp();
        }
    }
}