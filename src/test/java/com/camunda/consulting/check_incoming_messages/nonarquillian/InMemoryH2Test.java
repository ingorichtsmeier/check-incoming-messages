package com.camunda.consulting.check_incoming_messages.nonarquillian;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.camunda.consulting.check_incoming_messages.EinzelnachrichtPruefenDelegate;
import com.camunda.consulting.check_incoming_messages.EinzelnachrichtSpeichernDelegate;
import com.camunda.consulting.check_incoming_messages.KorrekturAnlegenDelegate;
import com.camunda.consulting.check_incoming_messages.KorrekturMeldenDelegate;
import com.camunda.consulting.check_incoming_messages.LieferungErstellenDelegate;
import com.camunda.consulting.check_incoming_messages.LoggerDelegate;
import com.camunda.consulting.check_incoming_messages.ResteEinsammelnDelegate;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

  @ClassRule
  @Rule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  private static final String PROCESS_DEFINITION_KEY = "check-incoming-messages";

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Before
  public void setup() {
    init(rule.getProcessEngine());
    Mocks.register("logger", new LoggerDelegate());
    Mocks.register("lieferungErstellenDelegate", new LieferungErstellenDelegate());
    Mocks.register("einzelnachrichtPruefenDelegate", new EinzelnachrichtPruefenDelegate());
    Mocks.register("einzelnachrichtSpeichernDelegate", new EinzelnachrichtSpeichernDelegate());
    Mocks.register("korrekturAnlegenDelegate", new KorrekturAnlegenDelegate());
    Mocks.register("korrekturMeldenDelegate", new KorrekturMeldenDelegate());
    Mocks.register("resteEinsammelnDelegate", new ResteEinsammelnDelegate());
  }

  /**
   * Just tests if the process definition is deployable.
   */
  @Test
  @Deployment(resources = "process.bpmn")
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testHappyPath() {
	  //ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    Map<String, Object> variables = withVariables(
        "nachrichtenliste", Arrays.asList("nachricht 1", "nachricht 2", "nachricht 3"),
        "nachrichtenId", 15);
    
    ProcessInstanceWithVariables processInstanceWithVariables = runtimeService()
        .createProcessInstanceByKey(PROCESS_DEFINITION_KEY)
        .setVariables(variables)
        .executeWithVariablesInReturn();
    	  
	  // Now: Drive the process by API and assert correct behavior by camunda-bpm-assert
    assertThat(processInstanceWithVariables.getVariables()).contains(
        entry("lieferliste", Arrays.asList("nachricht 1 verarbeitet", "nachricht 2 verarbeitet", "nachricht 3 verarbeitet")));
    assertThat(processInstanceWithVariables).isEnded();
  }
  
  @Test
  @Deployment(resources = "process.bpmn")
  public void testEineNachrichtKorrigieren() {
    Map<String, Object> variables = withVariables(
        "nachrichtenliste", Arrays.asList("nachricht 1", "nachricht 2", "nachricht 50"),
        "nachrichtenId", 15L);
    
    ProcessInstanceWithVariables processInstanceWithVariables = runtimeService()
        .createProcessInstanceByKey(PROCESS_DEFINITION_KEY)
        .setVariables(variables)
        .executeWithVariablesInReturn();
        
    assertThat(processInstanceWithVariables).isWaitingAt("reste_einsammeln_receive_task");
    
    ProcessInstance korrekturProzess = runtimeService().createProcessInstanceQuery().processDefinitionKey("korrektur").singleResult();
    assertThat(korrekturProzess)
      .isWaitingAt("einzelnachricht_korrigieren_user_task")
      .variables().containsEntry("nachrichtZurKorrektur", "nachricht 50");

    HistoricProcessInstance einzelnachrichtProzess = historyService().createHistoricProcessInstanceQuery()
        .processDefinitionKey("einzelnachricht")
        .variableValueEquals("nachricht", "nachricht 50")
        .singleResult();
    HistoricVariableInstance korrekturProzessId = historyService().createHistoricVariableInstanceQuery().processInstanceId(einzelnachrichtProzess.getId()).variableName("korrekturProzessId").singleResult();
    assertThat(korrekturProzessId.getValue()).isEqualTo(korrekturProzess.getId());
    
    complete(task(korrekturProzess), withVariables(
        "nachrichtKorrigiert", "nachricht 50 korrigiert", 
        "korrigiert", true));
    
    assertThat(processInstanceWithVariables).isEnded();
    HistoricVariableInstance lieferliste = historyService().createHistoricVariableInstanceQuery().processInstanceId(processInstanceWithVariables.getId()).variableName("lieferliste").singleResult();
    
    assertThat(lieferliste.getValue()).isEqualTo(
        Arrays.asList("nachricht 1 verarbeitet", "nachricht 2 verarbeitet", "nachricht 50 korrigiert"));
  }

}
