package com.github.zahisuku.powergrid_illuminations.electricity.sim.special;

import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;
import org.patryk3211.powergrid.electricity.sim.special.PNJunctionWire;

import static org.patryk3211.powergrid.electricity.sim.ElectricalNetwork.G_MIN;

public class SwitchedPNJunctionWire extends PNJunctionWire {
    protected double resistance;
    public static final double OFF_CONDUCTANCE = G_MIN * 0.5;

    private boolean state;
    private int stamp;

    
    // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, node1, node2
    public SwitchedPNJunctionWire(double reverseSaturationCurrent, double seriesResistance, double temperatureCelsius, double idealityFactor, IElectricNode node1, IElectricNode node2) {
        // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, node1, node2
        super(reverseSaturationCurrent,seriesResistance, temperatureCelsius, idealityFactor,node1,node2);
        state = true;
    }

    // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, node1, node2, 初期状態
    public SwitchedPNJunctionWire(double reverseSaturationCurrent, double seriesResistance, double temperatureCelsius, double idealityFactor, IElectricNode node1, IElectricNode node2, boolean initialState) {
        // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, node1, node2
        super(reverseSaturationCurrent,seriesResistance, temperatureCelsius, idealityFactor,node1,node2);
        state = initialState;
    }

    // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, 絶縁破壊電圧, 降伏・飽和電流, node1, node2
    public SwitchedPNJunctionWire(double reverseSaturationCurrent, double seriesResistance, double temperatureCelsius, double idealityFactor, double breakdownVoltage, double breakdownSaturationCurrent, IElectricNode node1, IElectricNode node2){
        // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, 絶縁破壊電圧, 降伏・飽和電流, node1, node2
        super(reverseSaturationCurrent,seriesResistance, temperatureCelsius, idealityFactor, breakdownVoltage, breakdownSaturationCurrent,node1,node2);
        state = true;
    }

    // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, 絶縁破壊電圧, 降伏・飽和電流, node1, node2, 初期状態
    public SwitchedPNJunctionWire(double reverseSaturationCurrent, double seriesResistance, double temperatureCelsius, double idealityFactor, double breakdownVoltage, double breakdownSaturationCurrent, IElectricNode node1, IElectricNode node2, boolean initialState){
        // 逆方向飽和電流, 直列抵抗, 温度（摂氏）, 理想係数, 絶縁破壊電圧, 降伏・飽和電流, node1, node2
        super(reverseSaturationCurrent,seriesResistance, temperatureCelsius, idealityFactor, breakdownVoltage, breakdownSaturationCurrent,node1,node2);
        state = initialState;
    }

    public void setState(boolean state){
        if(this.state != state){
            this.state = state;
            if(network != null) {
                if(state) {
                    // Switch is now on, add its conductance
                    network.updateConductance(this, super.conductance() - OFF_CONDUCTANCE);
                } else {
                    // Switch is now off, remove its conductance
                    network.updateConductance(this, -super.conductance() + OFF_CONDUCTANCE);
                }
                stamp = network.getStamp();
            }
        }
    }
    
    @Override
    public boolean isConverged() {
        return network != null && network.getStamp() != stamp;
    }

    public boolean getState() {
        return this.state;
    }
    
    @Override
public void startIteration(int iteration) {
    super.startIteration(iteration);
}


    public double getResistance() {
        return state ? 1.0 / super.conductance() : 1.0 / OFF_CONDUCTANCE;
    }

    @Override
    public double current() {
        return state ? super.current() : 0;
    }

    @Override
    public double conductance() {
        return state ? super.conductance() : OFF_CONDUCTANCE;
    }

    @Override
    public String toString() {
        return String.format("SwitchedPNJunctionWire(R=%g,%s,", resistance, state ? "ON" : "OFF") + super.toString() + ")";
    }
}