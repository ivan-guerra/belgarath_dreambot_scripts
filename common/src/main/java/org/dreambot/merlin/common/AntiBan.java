package org.dreambot.merlin.common;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Logger;

public class AntiBan {
    private final AbstractScript script;

    public AntiBan(AbstractScript script) {
        this.script = script;
    }

    public void run() {
        int roll = Calculations.random(0, 100);
        if (roll < 2) {
            int period_ms = Calculations.random(120000, 180000);
            Logger.log("Anti-ban: Going AFK for " + period_ms / 1000 + "sec");
            script.sleep(period_ms);
        }
    }
}
