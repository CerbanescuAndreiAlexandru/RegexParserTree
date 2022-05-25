import java.util.Vector;

public class Expression
{
   private String sExp;
   private boolean isValid;
   private int nposError;
   private String nposErrorDescription;
   private Vector<Parser.StateElement> vTree = null;

   public Expression(String sExp, boolean isValid,int nposError,String nsposErrorDescription)
   {
      this.sExp = sExp;
      this.isValid = isValid;
      this.nposError = nposError;
      this.nposErrorDescription = nsposErrorDescription;
   }

   public String getsExp()
   {
      return sExp;
   }

   public String getNposErrorDescription()
   {
      return nposErrorDescription;
   }

   public int getNposError()
   {
      return nposError;
   }

   public void setTree(Vector<Parser.StateElement> vTree) {
      this.vTree = vTree;
   }

   public Vector<Parser.StateElement> getTree() {
      return vTree;
   }

   public boolean getIsValid() {
      return isValid;
   }
}
