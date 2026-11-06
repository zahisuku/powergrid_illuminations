
package com.github.zahisuku.electricity.sim.special;

import com.github.zahisuku.powergrid_illuminations.electricity.sim.special.SwitchedPNJunctionWire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import java.util.List;

class SwitchedPNJunctionTests extends TestHelper {

    private static final double IS = 1e-26;
    private static final double SERIES_RESISTANCE = 24;
    private static final double TEMPERATURE = 25.0;
    private static final double IDEALITY_FACTOR = 1.95;

    /**
     * ON状態で回路計算を実行できることを確認する。
     */
    @Test
    void onStateCircuitCalculation() {
        Network net = new Network();

        var anode = net.C(0.001f);
        var cathode = net.V(0.0f);
        
        var diode = new SwitchedPNJunctionWire(
                IS, SERIES_RESISTANCE, TEMPERATURE,
                IDEALITY_FACTOR,
                5, 1e-5,
                anode, cathode
        );

        net.network.addWire(diode);

        for (int i = 0; i < 4; i++) {
            net.calculate();
        }

        System.out.printf("Diode current: %f mA\n", diode.current()*1000);
        System.out.printf("Diode voltage: %f V\n", diode.potentialDifference());
        System.out.printf("Diode state: %b\n", diode.getState());

        List<Float> list = List.of(0.005f,0.01f,0.02f,0.03f);

        for(Float current : list){
            anode.setCurrent(current);

            for(int i=0;i<1;i++) {
                net.calculate();
            }

            System.out.printf("\nDiode current: %f mA\n", diode.current()*1000);
            System.out.printf("Diode voltage: %f V\n", diode.potentialDifference());
            System.out.printf("Diode state: %b\n", diode.getState());
        }

        assertTrue(net.network.isConverged(), "PN junction failed to converge");
        assertTrue(Double.isFinite(anode.getVoltage()));
        assertTrue(Double.isFinite(cathode.getVoltage()));
        assertTrue(Double.isFinite(diode.current()));
    }
}
