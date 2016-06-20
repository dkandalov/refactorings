import com.intellij.navigation.ChooseByNameContributor
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.refactoring.JavaRefactoringFactory
import com.intellij.refactoring.RefactoringFactory

import static liveplugin.PluginUtil.runWriteAction
import static liveplugin.PluginUtil.show

def allScope = GlobalSearchScope.allScope(project)
def psiFacade = JavaPsiFacade.getInstance(project)
assert psiFacade.findClasses("JavaPsiFacade", allScope).toList().empty
assert psiFacade.findClasses("com.intellij.psi.JavaPsiFacade", allScope).toList().size() == 1

def refactorings = new Refactorings(project)

def aClass = refactorings.typeByName("AClass").first()
assert aClass.qualifiedName == "apackage.AClass"
assert aClass.methods.collect{it.name} == ["AClass", "interfaceMethod", "classMethod"]
assert aClass.allMethods.collect{it.name} == [
		"AClass", "interfaceMethod", "classMethod", "superClassMethod",
		"Object", "registerNatives", "getClass", "hashCode", "equals", "clone", "toString", "notify", "notifyAll",
		"wait", "wait", "wait", "finalize", "interfaceMethod"
]

def superClass = refactorings.typeByName("ASuperClass").first()
assert refactorings.findSubClassesOf(superClass).collect{it.qualifiedName} == ["apackage.AClass", "apackage.ASubClass"]

def referencesToClass = refactorings.referencesTo(aClass)
assert referencesToClass.collect{ it.element.text } == ["AClass"]

if (false) {
	def element = referencesToClass.first().element
	refactorings.replace(element, "ASuperClass")
}

if (true) {
	def rename = RefactoringFactory.getInstance(project).createRename(aClass, aClass.name + "Renamed")
	rename.interactive = null
	rename.previewUsages = false
	rename.searchInComments = true

	show(rename.run())
}

if (false) {
	show(aClass.methods.find{ it.constructor })
	def replaceConstructorWithFactory = JavaRefactoringFactory.getInstance(project)
			.createReplaceConstructorWithFactory(aClass.methods.find{ it.constructor }, aClass, "factory")
	replaceConstructorWithFactory.run()
}

//SelectionModel sel = getEditor().getSelectionModel();
//getEditor().getDocument().replaceString(sel.getSelectionStart(), sel.getSelectionEnd(), with);
//PsiDocumentManager.getInstance(getProject()).commitAllDocuments();


show("Current project: ${project.name}")


class Refactorings {
	private final Project project

	Refactorings(Project project) {
		this.project = project
	}

	Collection<PsiClass> typeByName(String className) {
		def allScope = GlobalSearchScope.allScope(project)
		def psiFacade = JavaPsiFacade.getInstance(project)
		def classes = psiFacade.findClasses(className, allScope).toList()
		if (!classes.empty) return classes

		def extensions = ChooseByNameContributor.CLASS_EP_NAME.extensions
		extensions
			.collectMany { it.getItemsByName(className, "", project, true).toList() }
			.unique()
	}

	Collection<PsiClass> findSubClassesOf(PsiClass psiClass) {
		ClassInheritorsSearch.search(psiClass).findAll()
	}

	Collection<PsiReference> referencesTo(PsiElement element) {
		ReferencesSearch.search(element).findAll()
	}

	def replace(PsiElement element, String withCode) {
		runWriteAction {
			CommandProcessor.instance.executeCommand(project, {
				def document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile)
				def from = element.textOffset
				def to = from + element.textLength
				document.replaceString(from, to, withCode)
			}, "Replaced ${element}", Refactorings.simpleName, UndoConfirmationPolicy.DEFAULT, null)
		}
	}
}

// com.intellij.refactoring.RefactoringFactory and JavaRefactoringFactory
// com.intellij.refactoring.BaseRefactoringProcessor.doRun can open UI dialogs etc

// com.intellij.psi.JavaPsiFacade - can search by short of qualified name
// com.intellij.psi.PsiElementFinder - can find methods/classes by fully qualified name
// ChooseByNameContributor.CLASS_EP_NAME - seems that for java it can only search by class name (without package);
//                                         uses com.intellij.psi.search.PsiShortNamesCache

// com.intellij.psi.search.searches.ReferencesSearch
// com.intellij.psi.search.searches.DefinitionsScopedSearch
// com.intellij.psi.util.ClassUtil
// com.intellij.navigation.ChooseByNameContributor

// com.intellij.ide.actions.GotoClassAction pattern matching is hard to reuse

// com.intellij.psi.search.PsiSearchHelper - probably used by text search dialog
