package com.camunda.consulting.check_incoming_messages;

import java.util.List;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class LieferungErstellenDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    List<String> lieferliste = (List<String>) execution.getVariable("lieferliste");
    Boolean vollstaendig = true;
    for (String liefereintrag : lieferliste) {
      if (liefereintrag != null && liefereintrag.contains("Fehler")) {
        vollstaendig = false;
      }
      if (liefereintrag == null) {
        vollstaendig = false;
      }
    }
    execution.setVariable("vollstaendig", vollstaendig);
  }

}
