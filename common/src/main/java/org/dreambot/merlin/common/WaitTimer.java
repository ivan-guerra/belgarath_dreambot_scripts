package org.dreambot.merlin.common;

import org.dreambot.api.methods.Calculations;

/**
 * Provides human-like randomised wait times using a Gamma distribution. On construction the base
 * shape and scale parameters are jittered so that every script run produces a subtly different
 * timing profile. A hard minimum floor is then added to every sample so that the bot never reacts
 * unrealistically fast.
 */
public class WaitTimer {
  /** Default base shape (k) for the Gamma distribution. */
  private static final int DEFAULT_BASE_SHAPE = 3;

  /** Default base scale (θ) for the Gamma distribution, in milliseconds. */
  private static final int DEFAULT_BASE_SCALE = 300;

  /** Maximum ± jitter applied to the shape parameter as a fraction of the base value (10%). */
  private static final double SHAPE_JITTER_FRACTION = 0.10;

  /** Maximum ± jitter applied to the scale parameter as a fraction of the base value (15%). */
  private static final double SCALE_JITTER_FRACTION = 0.15;

  /** Effective shape used for this instance, after per-run jitter has been applied. */
  private final int shape;

  /** Effective scale used for this instance, after per-run jitter has been applied. */
  private final int scale;

  /** Hard minimum floor in milliseconds added to every sampled value. */
  private final int floorMs;

  /** Hard maximum ceiling in milliseconds. 0 means uncapped. */
  private final int ceilMs;

  /**
   * Constructs a WaitTimer with custom base parameters and per-run jitter.
   *
   * @param floorMs hard-minimum milliseconds added to every sample – must be &gt;= 0
   * @param ceilMs hard-maximum milliseconds (0 = uncapped) – must be &gt;= floorMs if &gt; 0
   */
  public WaitTimer(int floorMs, int ceilMs) {
    this.shape = jitter(DEFAULT_BASE_SHAPE, SHAPE_JITTER_FRACTION);
    this.scale = jitter(DEFAULT_BASE_SCALE, SCALE_JITTER_FRACTION);
    this.floorMs = floorMs;
    this.ceilMs = ceilMs;
  }

  /**
   * Samples a wait time from the Gamma distribution and applies the hard floor and optional
   * ceiling.
   *
   * @return wait time in milliseconds (always &gt;= {@code floorMs}, and &lt;= {@code ceilMs} if
   *     set)
   */
  public int next() {
    int value = floorMs + (int) Math.round(Calculations.nextGammaRandom(shape, scale));
    return (ceilMs > 0) ? Math.min(value, ceilMs) : value;
  }

  /**
   * Applies a uniformly distributed ± fractional jitter to {@code base}.
   *
   * @param base the original parameter value
   * @param fraction maximum relative deviation (e.g. 0.10 = ±10 %)
   * @return jittered value, clamped to a minimum of 1
   */
  private static int jitter(int base, double fraction) {
    double delta = base * fraction;
    int jittered = (int) Math.round(base + Calculations.random(-delta, delta));
    return Math.max(1, jittered);
  }
}
