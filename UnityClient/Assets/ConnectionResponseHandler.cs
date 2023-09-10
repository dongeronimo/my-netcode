using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ConnectionResponseHandler 
{
    /// <summary>
    /// Can only handle if the payload is exactly "connection_ack"
    /// </summary>
    /// <param name="payload"></param>
    /// <returns></returns>
    public bool CanHandle(string payload)
    {
        if(payload.Contains("connection_ack"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    /// <summary>
    /// Changes global state to connected. Someone else will invoke callbacks
    /// </summary>
    /// <param name="payload"></param>
    public void Handle(string payload)
    {
        Debug.Log("Alterando o state pra connected");
        GameStateStorage.Connected();
    }
}
