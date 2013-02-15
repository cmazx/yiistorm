import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiElement;
import com.yiistorm.YiiPsiReferenceProvider;

public class MyPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        YiiPsiReferenceProvider provider = new YiiPsiReferenceProvider();

        //registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValue.class), provider);
        //registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlTag.class), provider);

        registrar.registerReferenceProvider(StandardPatterns.instanceOf(PsiElement.class), provider);
    }
}
