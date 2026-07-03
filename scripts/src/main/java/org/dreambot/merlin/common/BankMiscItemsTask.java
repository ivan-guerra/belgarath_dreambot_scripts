package org.dreambot.merlin.common;

import java.util.regex.Pattern;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

/**
 * Task node responsible for depositing all items in the bank except those that match a specified
 * regex pattern.
 */
public class BankMiscItemsTask extends TaskNode {
  private final String expectedItemRegex;

  /**
   * Constructs a new BankMiscItemsTask with the specified regex pattern for expected items.
   *
   * @param expectedItemRegex the regex pattern for items that should not be deposited
   */
  public BankMiscItemsTask(String expectedItemRegex) {
    this.expectedItemRegex = expectedItemRegex;
  }

  /**
   * Checks if there are any items in the inventory that do not match the expected item regex.
   *
   * @return true if there are items to deposit, false otherwise
   */
  @Override
  public boolean accept() {
    final Pattern pattern = Pattern.compile(expectedItemRegex, Pattern.CASE_INSENSITIVE);

    return Inventory.all().stream()
        .filter(item -> item != null)
        .anyMatch(item -> !pattern.matcher(item.getName()).find());
  }

  /**
   * Executes the task of depositing all items in the bank except those that match the expected item
   * regex.
   *
   * @return 1000 if the operation was successful, -1 if there was an error
   */
  @Override
  public int execute() {
    if (Bank.open()) {
      if (Bank.depositAllExcept(
          item ->
              item != null
                  && Pattern.compile(expectedItemRegex, Pattern.CASE_INSENSITIVE)
                      .matcher(item.getName())
                      .find())) {

        Logger.info("Deposited all items except those matching: " + expectedItemRegex);

        Bank.close();
        Sleep.sleepUntil(() -> !Bank.isOpen(), 2000);
      } else {
        Logger.error("Failed to deposit items.");
        return -1;
      }
    }
    return 1000;
  }
}
