package edu.ntnu.idatt2106.project.sparesti.services;

/** Service interface for TestId-entities. */
public interface TestIdCounterService {
  /**
   * Get the next incremental id to use as a unique id.
   *
   * @return The unique id.
   */
  long getNextUniqueId();
}
