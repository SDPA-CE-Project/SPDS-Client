package com.example.spda_app.DAO;

public class Hello {
    static Hello instance;

    public int info;
    public static Hello GetInstance()
    {
        if(instance==null)
        {
            instance = new Hello();
        }
        return instance;
    }
}