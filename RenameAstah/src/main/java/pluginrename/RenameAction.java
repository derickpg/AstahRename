package pluginrename;


import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

/*
 * Please change this class's package to your genearated Plug-in's package.
 * Plug-in's package namespace => com.example
 *   com.change_vision.astah.extension.plugin => X
 *   com.example                              => O
 *   com.example.internal                     => O
 *   learning                                 => X
 */



import javax.swing.JOptionPane;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class RenameAction implements IPluginActionDelegate {

  public Object run(IWindow window) throws UnExpectedException {
      try {
    	  AstahAPI api = AstahAPI.getAstahAPI();
    	  ProjectAccessor projectAccessor = api.getProjectAccessor();
    	  projectAccessor.getProject();
          IModel iCurrentProject = projectAccessor.getProject();
          String nomeclasse = JOptionPane.showInputDialog("Qual a classe a ser refatorada?");
          IClass classe = busca(iCurrentProject, nomeclasse);
          if (classe == null)
            JOptionPane.showMessageDialog(window.getParent(),
            		"A classe " + nomeclasse + " não existe!" , "Error", JOptionPane.ERROR_MESSAGE);
          else {
        	String novaclasse = JOptionPane.showInputDialog("Qual o novo nome da classe?");
        	IClass verifica = busca(iCurrentProject, novaclasse);
        	if(verifica == null) {
        		classe.setName(novaclasse);
        		Boolean construtor = alteraConstrutor(classe, novaclasse);
        		String construtoresmsg;
        		if(construtor) {
        			construtoresmsg = "Os construtores foram alterados com sucesso!";
        		}
        		else {
        			construtoresmsg = "Não foi alterado nenhum construtor!";
        		}
                JOptionPane.showMessageDialog(window.getParent(),
                        "Classe alterada com sucesso! \n Antigo nome: " + nomeclasse + " \n Novo nome: " + novaclasse + 
                        " \n " + construtoresmsg + "."
                        	, "Success", JOptionPane.INFORMATION_MESSAGE);
        	}
        	else {
        		JOptionPane.showMessageDialog(window.getParent(),
        	              "A classe " + novaclasse + " já existe!", "Error", JOptionPane.ERROR_MESSAGE);
        	}
          }

      } catch (Exception e) {
        JOptionPane.showMessageDialog(window.getParent(), "Exception occured", "Alert", JOptionPane.ERROR_MESSAGE);
          throw new UnExpectedException();
      }
      return null;
  }
  
  private Boolean alteraConstrutor(IClass classe, String novonome) throws InvalidEditingException{
	  IOperation[] op = classe.getOperations();
	  Boolean retorno = false;
	  for (int i = 0; i < op.length; i++) {
		  String[] is = op[i].getStereotypes();
		  for (int j = 0; j < is.length; j++) {
			if ("create".equalsIgnoreCase(is[j])) {
				op[i].setName(novonome);
				retorno = true;
			}
		}
	}
	  return retorno;
  }
  
  
  private IClass busca(IModel iCurrentProject, String nome) {
      List<IClass> classList = new ArrayList<IClass>();
      getAllClasses(iCurrentProject, classList);
      StringBuffer error = new StringBuffer();
      boolean hasError = false;
      for (IClass cl : classList) {
        if (nome.equals(cl.getName())) {
        	return cl;
        }
      }
        return null;
    }
   
    private void getAllClasses(INamedElement element, List<IClass> classList) {
      if (element instanceof IPackage) {
        for (INamedElement ownedNamedElement : ((IPackage) element).getOwnedElements()) {
          getAllClasses(ownedNamedElement, classList);
        }
      } else if (element instanceof IClass) {
        classList.add((IClass) element);
      }
    }
  
}