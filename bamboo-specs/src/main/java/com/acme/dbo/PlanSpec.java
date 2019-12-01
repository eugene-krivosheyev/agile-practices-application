package com.acme.dbo;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.permission.*;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.*;
import com.atlassian.bamboo.specs.api.builders.task.AnyTask;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;

/**
 * Plan configuration for Bamboo.
 * @see <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">Bamboo Specs</a>
 *
 * Test and run with -Dbamboo.specs.log.level=DEBUG
 */
@BambooSpec
public class PlanSpec {
    /**
     * To publish your plan:
     * run 'main' or mvn -Ppublish-specs
     */
    public static void main(String... args) throws Exception {
        final BambooServer bambooServer = new BambooServer("http://84.201.134.115:8085"); // by default credentials are read from the '.credentials' file

        final PlanSpec planSpec = new PlanSpec();
        bambooServer.publish(planSpec.buildPlan());
        bambooServer.publish(planSpec.buildPlanPermissions());
        bambooServer.publish(planSpec.deployPlan());
        bambooServer.publish(planSpec.deployPlanPermissions());
        bambooServer.publish(planSpec.environmentsPermissions());
    }


    public Project project() {
        return new Project()
                .name("dbo")
                .key("DBO");
    }

    public Plan buildPlan() {
        return new Plan(project(), "build", "BUILD")
                .pluginConfigurations(new ConcurrentBuilds())
                .stages(new Stage("build-stage")
                        .jobs(new Job("make-distr", "MAKEDISTR")
                                .artifacts(new Artifact()
                                        .name("dbo-distr")
                                        .copyPattern("dbo-1.0-SNAPSHOT.jar")
                                        .location("target")
                                        .shared(true)
                                        .required(true))
                                .tasks(new VcsCheckoutTask()
                                                .description("checkout")
                                                .checkoutItems(new CheckoutItem().defaultRepository()),
                                        new MavenTask()
                                                .description("maven-full-build")
                                                .goal("clean verify")
                                                .environmentVariables("MAVEN_OPTS=\"-Xms512m -Xmx512m -Dlogback.configurationFile=logback-quiet.xml -Djava.awt.headless=true\"")
                                                .jdk("JDK 1.8")
                                                .executableLabel("Maven 3.6")
                                                .hasTests(true)
                                                .testResultsPath("**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml"),
                                        new MavenTask()
                                                .description("run-sonar-analysis")
                                                .goal("sonar:sonar")
                                                .environmentVariables("MAVEN_OPTS=\"-Dsonar.host.url=http://84.201.134.115:9000 -Dsonar.login=2128229f6d1e19c70ff3f2c8df0ad74e382934d1 -Djava.awt.headless=true\"")
                                                .jdk("JDK 1.8")
                                                .executableLabel("Maven 3.6"),
                                        new MavenTask()
                                                .description("upload-distr-to-artifactiry")
                                                .goal("deploy")
                                                .environmentVariables("MAVEN_OPTS=\"-DskipTests -Dlogback.configurationFile=logback-quiet.xml -Djava.awt.headless=true\"")
                                                .jdk("JDK 1.8")
                                                .executableLabel("Maven 3.6"))))
                .linkedRepositories("dbo-app-master")
                .triggers(new BitbucketServerTrigger())
                .planBranchManagement(new PlanBranchManagement()
                        .delete(new BranchCleanup())
                        .notificationForCommitters());
    }

    public Deployment deployPlan() {
        return new Deployment(buildPlan().getIdentifier(),"deploy")
                .releaseNaming(new ReleaseNaming("release-1")
                        .autoIncrement(true))
                .environments(new Environment("pre-prod")
                        .tasks(new CleanWorkingDirectoryTask()
                                        .description("clean"),
                                new MavenTask()
                                        .description("distr-download")
                                        .goal("org.apache.maven.plugins:maven-dependency-plugin:2.4:get")
                                        .environmentVariables("MAVEN_OPTS=\"-Dtransitive=false -Ddest=/home/dboadmin/dbo/dbo-1.0-SNAPSHOT.jar -DremoteRepositories=dbo-artifacts-server::::http://84.201.134.115:8081/artifactory/dbo -Dartifact=com.acme.banking:dbo:1.0-SNAPSHOT\"")
                                        .jdk("JRE 1.8")
                                        .executableLabel("Maven 3.6"),
                                new ScriptTask()
                                        .description("restart-service")
                                        .inlineBody("sudo systemctl restart dbo-app"))
                        .triggers(new AfterSuccessfulBuildPlanTrigger()
                                        .triggerByMasterBranch()));
    }

    public PlanPermissions buildPlanPermissions() {
        return new PlanPermissions(buildPlan().getIdentifier())
                .permissions(new Permissions()
                        .userPermissions("admin", PermissionType.ADMIN, PermissionType.EDIT, PermissionType.VIEW, PermissionType.CLONE, PermissionType.BUILD)
                        .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                        .loggedInUserPermissions(PermissionType.BUILD)
                        .anonymousUserPermissionView()
                );
    }

    public DeploymentPermissions deployPlanPermissions() {
        return new DeploymentPermissions(deployPlan().getName())
                .permissions(new Permissions()
                        .userPermissions("admin", PermissionType.EDIT, PermissionType.VIEW));
    }

    public EnvironmentPermissions environmentsPermissions() {
        return  new EnvironmentPermissions(deployPlan().getName())
                .environmentName("pre-prod")
                .permissions(new Permissions()
                        .userPermissions("admin", PermissionType.EDIT, PermissionType.VIEW, PermissionType.BUILD));
    }
}
