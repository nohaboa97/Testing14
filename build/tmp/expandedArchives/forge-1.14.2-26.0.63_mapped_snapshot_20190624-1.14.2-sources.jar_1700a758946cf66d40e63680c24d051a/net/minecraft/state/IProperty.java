package net.minecraft.state;

import java.util.Collection;
import java.util.Optional;

public interface IProperty<T extends Comparable<T>> {
   String getName();

   Collection<T> getAllowedValues();

   /**
    * The class of the values of this property
    */
   Class<T> getValueClass();

   Optional<T> parseValue(String value);

   /**
    * Get the name for the given value.
    */
   String getName(T value);
}