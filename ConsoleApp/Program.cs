using ConsoleApp;
using System;

namespace MyApp // Note: actual namespace depends on the project name.
{
    internal class Program
    {
        private static void HelloUDP()
        {
            var hello = new HelloWorldUDP("localhost", 11111);
            hello.DoIt();
        }
        static void Main(string[] args)
        {
            HelloUDP();
        }
    }
}