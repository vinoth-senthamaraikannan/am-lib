package uk.gov.hmcts.reform.amlibtestingapi;

import uk.gov.hmcts.reform.amlib.DummyService;

public class MyDummyService {

    public String getHello() {
        return new DummyService().getHello();
    }
}
