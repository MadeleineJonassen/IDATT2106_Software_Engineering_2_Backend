package edu.ntnu.idatt2106.project.sparesti.services.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.entities.TestIdCounterEntity;
import edu.ntnu.idatt2106.project.sparesti.repositories.TestIdCounterRepository;
import edu.ntnu.idatt2106.project.sparesti.services.TestIdCounterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of the TestIdCounterService. */
@Service
public class TestIdCounterServiceImpl implements TestIdCounterService {
  private TestIdCounterRepository idCounterRepository;

  public TestIdCounterServiceImpl(TestIdCounterRepository idCounterRepository) {
    this.idCounterRepository = idCounterRepository;
  }

  /**
   * {@inheritDoc}
   *
   * @return The next unique Id.
   */
  @Transactional
  public synchronized long getNextUniqueId() {
    TestIdCounterEntity testIdCounter = new TestIdCounterEntity();
    TestIdCounterEntity createdTestIdCounter = idCounterRepository.save(testIdCounter);
    return createdTestIdCounter.getId();
  }
}
