package com.github.zahisuku.powergrid_illuminations.electricity;

import com.github.zahisuku.powergrid_illuminations.electricity.sim.special.SwitchedPNJunctionWire;

public interface ILedFixtureEntity {
    SwitchedPNJunctionWire getFilament();

    void setPowerLevel(int bulbPower);
    int getPowerLevel();
}
