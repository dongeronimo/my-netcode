using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using UniRx;
using UnityEngine;

public enum ConnectionState { NotConnected, Connecting, Connected }
public struct GameState : ICloneable
{
    public ConnectionState connectionState;

    public object Clone()
    {
        return new GameState { connectionState = connectionState };
    }
}

public static class GameStateStorage
{
    public static BehaviorSubject<GameState> state = new BehaviorSubject<GameState>(
        new GameState { connectionState = ConnectionState.NotConnected });

    public static void Disconnected()
    {
        var newState = (GameState)state.Value.Clone();
        newState.connectionState = ConnectionState.NotConnected;
        state.OnNext(newState);
    }
    public static void Connecting()
    {
        var newState = (GameState)state.Value.Clone();
        newState.connectionState = ConnectionState.Connecting;
        state.OnNext(newState);
    }

    public static void Connected()
    {
        var newState = (GameState)state.Value.Clone();
        newState.connectionState = ConnectionState.Connected;
        state.OnNext(newState);
    }
}
