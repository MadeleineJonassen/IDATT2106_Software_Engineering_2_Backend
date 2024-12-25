package edu.ntnu.idatt2106.project.sparesti.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An Entity holding information about a bank account Object. */
@Entity
@Getter
@Setter
@Builder
@Table(name = "bank_account")
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountEntity {

  @Id private Long id;

  private String name;

  private double sum;

  @OneToMany(
      mappedBy = "bankAccount",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<TransactionEntity> transactions;

  @OneToMany(mappedBy = "sourceBankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<TransferEntity> transfersSent;

  @OneToMany(mappedBy = "destinationBankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<TransferEntity> transfersReceived;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;

  /**
   * Adds a transaction to the list of transactions for this bank account. If the list is null, it
   * initializes a new list before adding the transaction. Sets the bank account reference in the
   * transaction object to this bank account.
   *
   * @param transaction The transaction to add to this bank account.
   */
  public void addTransaction(TransactionEntity transaction) {
    if (transaction != null) {
      if (transactions == null) {
        transactions = new ArrayList<>();
      }
      transactions.add(transaction);
      transaction.setBankAccount(this);
    }
  }

  /**
   * Adds a received transfer to the list of received transfers for this bank account. If the list
   * is null, it initializes a new list before adding the transfer. Sets the destination bank
   * account reference in the transfer object to this bank account.
   *
   * @param transfer The transfer received to add to this bank account.
   */
  public void addTransferReceived(TransferEntity transfer) {
    if (transfer != null) {
      if (transfersReceived == null) {
        transfersReceived = new ArrayList<>();
      }
      transfersReceived.add(transfer);
      transfer.setDestinationBankAccount(this);
    }
  }

  /**
   * Adds a sent transfer to the list of sent transfers for this bank account. If the list is null,
   * it initializes a new list before adding the transfer. Sets the source bank account reference in
   * the transfer object to this bank account.
   *
   * @param transfer The transfer sent to add to this bank account.
   */
  public void addTransferSent(TransferEntity transfer) {
    if (transfer != null) {
      if (transfersSent == null) {
        transfersSent = new ArrayList<>();
      }
      transfersSent.add(transfer);
      transfer.setSourceBankAccount(this);
    }
  }
}
