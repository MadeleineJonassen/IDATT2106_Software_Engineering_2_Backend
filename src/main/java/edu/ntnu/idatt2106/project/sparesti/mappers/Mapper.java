package edu.ntnu.idatt2106.project.sparesti.mappers;

/**
 * An Interface used for defining mappings between DTOs and Entities.
 *
 * @param <A> First class.
 * @param <B> Second class.
 */
public interface Mapper<A, B> {
  /**
   * Takes in an object of Class A and maps it to Class B.
   *
   * @param a Object to convert.
   * @return Converted object.
   */
  B mapTo(A a);

  /**
   * Takes in an object of Class B and maps it to Class A.
   *
   * @param b Object to convert.
   * @return Converted object.
   */
  A mapFrom(B b);
}
