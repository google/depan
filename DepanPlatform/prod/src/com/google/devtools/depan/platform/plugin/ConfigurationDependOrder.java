package com.google.devtools.depan.platform.plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Provide configuration entries in dependency order.  Order is defined
 * by a configuration item tag and attribute.  A viable partial order is
 * returned.  Items that cannot be placed into the dependent order can be
 * retrieved by the {@link #getRemains()} call.  The method will return
 * an empty list if all supplied {@link ContributionEntry}s are placed
 * into the {@link #getDependOrder()} result.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class ConfigurationDependOrder<T> {

  private final String dependTag;

  private final String dependAttr;

  private final List<T> dependOrder = Lists.newArrayList();

  private final Set<String> contribIds = Sets.newHashSet();

  private Collection<ContributionEntry<T>> remains;

  /**
   * Define the XML tag and attribute used to obtain contribution dependency
   * information.
   */
  public ConfigurationDependOrder(String dependTag, String dependAttr) {
    this.dependTag = dependTag;
    this.dependAttr = dependAttr;
  }

  /**
   * Provide the instances for the {@link ContributionEntry}s in
   * dependency order.
   */
  public List<T> getDependOrder() {
    return dependOrder;
  }

  /**
   * Provide the {@link ContributionEntry} that cannot be placed into the
   * {@link #getDependOrder()} result.
   * 
   * This result is reset after each call to
   * {@link #addContributions(Collection)}.  If that method is used multiple
   * times, this method should be checked after each call.
   */
  public Collection<ContributionEntry<T>> getRemains() {
    return remains;
  }

  /**
   * Add the supplied list of {@link ContributionEntry}s to the dependency
   * order result.
   * This can be called multiple times, and {@link #getRemains()} is
   * reset on each call.  If this method is used multiple times,
   * {@link #getRemains()} should be checked after each call.
   */
  public void addContributions(
      Collection<ContributionEntry<T>> contribs) {

    remains = contribs;
    int pendingCnt = remains.size();
    while (pendingCnt > 0) {
      remains = appendContributions(remains);
      int updateCnt = remains.size();
      if (updateCnt < pendingCnt) {
        pendingCnt = updateCnt;
      } else {
        return;
      }
    }
  }

  private Collection<ContributionEntry<T>> appendContributions(
      Collection<ContributionEntry<T>> contribs) {
    Collection<ContributionEntry<T>> pending =
        Lists.newArrayListWithExpectedSize(contribs.size());
    for (ContributionEntry<T> contrib : contribs) {
      if (isInstallable(contrib)) {
        dependOrder.add(contrib.getInstance());
        contribIds.add(contrib.getId());
      } else {
        pending.add(contrib);
      }
      
    }
    return pending;
  }

  private boolean isInstallable(
      ContributionEntry<T> contrib) {
    Collection<String> dependIds = contrib.getDependIds(dependTag, dependAttr);
    for (String dependId : dependIds) {
      if (!(contribIds.contains(dependId))) {
        return false;
      }
    }
    return true;
  }
}
