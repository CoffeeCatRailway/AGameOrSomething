package io.github.coffeecatrailway.agameorsomething.core.registry;

import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;

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
