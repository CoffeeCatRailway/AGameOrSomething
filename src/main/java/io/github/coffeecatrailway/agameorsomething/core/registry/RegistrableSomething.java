package io.github.coffeecatrailway.agameorsomething.core.registry;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public interface RegistrableSomething
{
    int getId();

    ObjectLocation getObjectId();

    void setId(int id, ObjectLocation objectId);

    RegistrableSomething getInstance();
}
