package me.fenixra.magic_altar.files;

import me.fenixra.magic_altar.utils.PhoenixFile;
import me.fenixra.magic_altar.utils.PhoenixFileClass;
import me.fenixra.magic_altar.utils.PhoenixFileManager;

public class ConfigFile extends PhoenixFile {
    public ConfigFile(PhoenixFileManager fileM) {
        super(fileM, "config", new ConfigClass());
    }
    @Override
    public boolean handleLoad() {
        return true;
    }

    @Override
    public boolean reloadAction() {
        return true;
    }



    public static class ConfigClass extends PhoenixFileClass {

        @ConfigHeader(value = {"#Sound type of the reward"})
        @ConfigKey(path="reward_sound", space= "      ")
        public static String reward_sound="ENTITY_PLAYER_LEVELUP";

        @ConfigHeader(value = { "#Sound param1 of the reward"})
        @ConfigKey(path="sound_param1", space= "      ")
        public static float sound_param1=1.0f;

        @ConfigHeader(value = { "#Sound param2 of the reward"})
        @ConfigKey(path="sound_param2", space= "      ")
        public static float sound_param2=1.0f;

        @ConfigHeader(value = { "Action_bar msg that shows how much time left before reward"})
        @ConfigKey(path="msg_reward_time_left", space= "      ")
        public static String msg_reward_time_left="§c{time}s §aLeft}";

        @ConfigHeader(value = { "Action_bar msg that appears when reward received"})
        @ConfigKey(path="msg_rewarded", space= "      ")
        public static String msg_rewarded="§aReceived";


        public Object getReference(int number) {
            Object[] ob = {reward_sound,sound_param1,sound_param2,msg_reward_time_left,msg_rewarded};
            return ob[number];
        }
    }
}
