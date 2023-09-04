using Newtonsoft.Json;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;

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
        StartCoroutine(login((token) => this.tokem = token, "alice", "blablabla"));
    }
    public void LoginWithBob()
    {
        StartCoroutine(login((token) => this.tokem = token, "bob", "blablabla"));
    }
    IEnumerator login(System.Action<string> afterLogin, string username, string password)
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
}
