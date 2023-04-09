package io.github.coffeecatrailway.orsomething.anengine.core.registry;

import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public interface RegistrableSomething
{
    int getId();

    ObjectLocation getObjectId();

    void setId(int id, ObjectLocation objectId);

    <T extends RegistrableSomething> T getInstance();
}
