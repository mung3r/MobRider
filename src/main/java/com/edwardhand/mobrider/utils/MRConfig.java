package com.edwardhand.mobrider.utils;

import com.edwardhand.mobrider.MobRider;

public class MRConfig
{
    public static int MAX_DISTANCE = 100;
    public static double MAX_FIND_RANGE = 100D;
    public static double ATTACK_RANGE = 10D;
    public static double MOUNT_RANGE = 3.0D;

    public static String AttackConfirmedMessage = "!";
    public static String AttackConfusedMessage = "?";
    public static String FollowConfirmedMessage = "!";
    public static String FollowConfusedMessage = "?";
    public static String GoConfirmedMessage = "";
    public static String GoConfusedMessage = "?";
    public static String StopConfirmedMessage = "";
    public static String CreatureFedMessage = " :D";

    private MobRider plugin;

    public MRConfig(MobRider plugin)
    {
        this.plugin = plugin;
    }
}
