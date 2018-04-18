package com.camunda.consulting.check_incoming_messages;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class EinzelnachrichtSpeichernDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    execution.setVariable("nachrichtVerarbeitet", 
        execution.getVariable("nachricht") + " verarbeitet");
  }

}
