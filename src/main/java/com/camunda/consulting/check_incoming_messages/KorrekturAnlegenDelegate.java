package com.camunda.consulting.check_incoming_messages;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;

@Named
public class KorrekturAnlegenDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String nachricht = (String) execution.getVariable("nachricht");
    Long nachrichtenId = (Long) execution.getVariable("nachrichtenId");
    ProcessInstance korrekturProzess = execution.getProcessEngineServices().getRuntimeService()
        .createMessageCorrelation("Korrektur starten message")
        .setVariable("nachrichtZurKorrektur", nachricht)
        .setVariable("nachrichtenId", nachrichtenId)
        .correlateStartMessage();
    execution.setVariable("korrekturProzessId", korrekturProzess.getId());
  }

}
