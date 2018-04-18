package com.camunda.consulting.check_incoming_messages;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class KorrekturMeldenDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String nachrichtKorrigiert = (String) execution.getVariable("nachrichtKorrigiert");
    Long nachrichtenId = (Long) execution.getVariable("nachrichtenId");
    execution.getProcessEngineServices().getRuntimeService()
      .createMessageCorrelation("Korrektur abgeschlossen message")
      .setVariable("nachrichtKorrigiert", nachrichtKorrigiert)
      .processInstanceVariableEquals("nachrichtenId", nachrichtenId)
      .correlate();
  }

}
