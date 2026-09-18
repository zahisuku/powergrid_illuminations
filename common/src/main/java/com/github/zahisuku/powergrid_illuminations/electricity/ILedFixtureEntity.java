package com.github.zahisuku.powergrid_illuminations.electricity;

import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public interface ILedFixtureEntity {
    SwitchedWire getFilament();

    void setPowerLevel(int bulbPower);
    int getPowerLevel();
}
