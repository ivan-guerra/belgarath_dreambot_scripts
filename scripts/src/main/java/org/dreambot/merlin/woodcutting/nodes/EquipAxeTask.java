package org.dreambot.merlin.woodcutting.nodes;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.common.WalkingUtils;
import org.dreambot.merlin.woodcutting.Axe;

/** Task node for equipping an axe in the game. */
public class EquipAxeTask extends TaskNode {
  private final AtomicReference<Axe> currAxe;
  private boolean buyingFromGE = false;

  /**
   * Constructs a new EquipAxeTask with the given AtomicReference to the current axe.
   *
   * @param currAxe an AtomicReference to the current axe being used
   */
  public EquipAxeTask(AtomicReference<Axe> currAxe) {
    this.currAxe = currAxe;
  }

  /**
   * Determines whether the player is currently wielding an axe.
   *
   * @return true if the player is not wielding an axe, false otherwise
   */
  @Override
  public boolean accept() {
    return !Utility.isEquipped(currAxe.get().getName());
  }

  /**
   * Equips the current axe if it is in the player's inventory, or withdraws it from the bank if
   * not. As a last resort, walks to the Grand Exchange and buys the axe.
   *
   * @return delay in milliseconds before next execution, or -1 on failure
   */
  @Override
  public int execute() {
    final String axeName = currAxe.get().getName();

    if (Utility.isInInventory(axeName)) {
      Logger.info(axeName + " found in inventory. Attempting to equip.");
      if (Utility.equipItem(axeName)) {
        Logger.info("Equipped " + axeName + ".");
      } else {
        Logger.error("Failed to equip " + axeName + ".");
        return -1;
      }
      return 3000;
    }

    if (buyingFromGE) {
      return buyFromGrandExchange(axeName);
    }

    if (Bank.open()) {
      if (Utility.withdrawItemFromBank(axeName)) {
        Logger.info("Withdrew " + axeName + " from bank.");
        Utility.closeBank();
      } else {
        Logger.info(axeName + " not found in bank. Falling back to Grand Exchange.");
        Utility.closeBank();
        buyingFromGE = true;
        return buyFromGrandExchange(axeName);
      }
    } else {
      buyingFromGE = true;
      return buyFromGrandExchange(axeName);
    }

    return 3000;
  }

  /**
   * Buys the specified axe from the Grand Exchange if it is not already in the player's inventory
   * or bank.
   *
   * @param axeName the name of the axe to buy
   * @return delay in milliseconds before next execution, or -1 on failure
   */
  private int buyFromGrandExchange(String axeName) {
    if (GrandExchange.isOpen()) {
      if (GrandExchange.isReadyToCollect()) {
        GrandExchange.collectToBank();
        Sleep.sleepUntil(() -> !GrandExchange.isReadyToCollect(), 5000);

        Logger.info("Collected items from Grand Exchange.");
        buyingFromGE = false;

        GrandExchange.close();
        Sleep.sleepUntil(() -> !GrandExchange.isOpen(), 5000);
      } else if (GrandExchange.getOpenSlots() > 0) {
        final int axeBuyPrice = LivePrices.getHigh(axeName);

        Logger.info("Buying " + axeName + " from Grand Exchange for " + axeBuyPrice + " coins.");
        if (!GrandExchange.buyItem(axeName, 1, axeBuyPrice)) {
          Logger.error("Failed to buy " + axeName + " from Grand Exchange.");
          return -1;
        }
      } else {
        Logger.error("No open Grand Exchange slots available.");
        return -1;
      }
    } else {
      WalkingUtils.walkToTile(BankLocation.GRAND_EXCHANGE.getTile());
      GrandExchange.open();
    }
    return 3000;
  }
}
