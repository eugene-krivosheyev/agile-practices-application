package com.acme.dbo;

import org.junit.Test;

import static com.atlassian.bamboo.specs.api.util.EntityPropertiesBuilders.build;

public class PlanSpecTest {
    @Test
    public void verifyPlanOffline() {
        PlanSpec planSpec = new PlanSpec();

        build(planSpec.project());

        build(planSpec.buildPlan());
        build(planSpec.buildPlanPermissions());

        build(planSpec.deployPlan());
        build(planSpec.deployPlanPermissions());
        build(planSpec.environmentsPermissions());
    }
}
