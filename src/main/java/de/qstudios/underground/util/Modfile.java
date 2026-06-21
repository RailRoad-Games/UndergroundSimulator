package de.qstudios.underground.util;

import de.qstudios.extmgr.ModfileImplementation;

public interface Modfile extends ModfileImplementation {
    public void activity();
    public void onEnable();
    public void onGameStart();
    public void onLaunch();
}
