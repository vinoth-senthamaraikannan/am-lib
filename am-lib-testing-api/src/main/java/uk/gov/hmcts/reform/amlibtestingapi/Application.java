package uk.gov.hmcts.reform.amlibtestingapi;

@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        System.out.println(new MyDummyService().getHello());
    }
}
