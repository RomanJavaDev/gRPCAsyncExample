package com.jeeconf.grpcdemo.providers;

import com.jeeconf.grpcdemo.Pressure;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Randomly generates {@link Pressure}.
 */
public class RandomPressureProvider implements Supplier<Pressure> {

    private final Random random = new Random();

    @Override
    public Pressure get() {
        float pressure = random.nextInt(99) + random.nextFloat();
        return Pressure.newBuilder().setValue(pressure).build();
    }
}
