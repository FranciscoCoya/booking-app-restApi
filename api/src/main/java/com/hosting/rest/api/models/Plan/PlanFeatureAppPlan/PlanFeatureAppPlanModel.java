package com.hosting.rest.api.models.Plan.PlanFeatureAppPlan;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@Table(name = "PLAN_FEATURE_APP_PLAN")
public class PlanFeatureAppPlanModel {

    @EmbeddedId
    private PlanFeatureAppPlanId planFeatureAppPlanId;
}
