/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ibapl.jnrheader.api.windows;

import de.ibapl.jnrheader.Defined;
import de.ibapl.jnrheader.api.windows.Minwindef_H.LPVOID;
import de.ibapl.jnrheader.api.windows.Minwindef_H.HANDLE;

/**
 *
 * @author aploese
 */
public class Minwinbase_H {

    public static class OVERLAPPED {

        public class DUMMYUNIONNAME {

            private DUMMYUNIONNAME(boolean withDUMMYSTRUCTNAME) {
                if (withDUMMYSTRUCTNAME) {
                    dummystructname = new DUMMYSTRUCTNAME();
                    Pointer = null;
                } else {
                    dummystructname = null;
                    Pointer = new LPVOID();
                }
            }

            public class DUMMYSTRUCTNAME {

                public long Offset;
                public long OffsetHigh;
            }

            public final DUMMYSTRUCTNAME dummystructname;
            public final LPVOID Pointer;
        }
        long Internal;
        long InternalHigh;
        public final DUMMYUNIONNAME dummyunionname;
        public HANDLE hEvent;

        private OVERLAPPED(boolean withDUMMYSTRUCTNAME) {
            dummyunionname = new DUMMYUNIONNAME(withDUMMYSTRUCTNAME);
        }

        public static OVERLAPPED newOLWithUnionDUMMYSTRUCTNAME() {
            return new OVERLAPPED(true);
        }

        public static OVERLAPPED newOLWithUnionPointer() {
            return new OVERLAPPED(false);
        }
    }

    public static class SECURITY_ATTRIBUTES {

        long nLength;
        LPVOID lpSecurityDescriptor;
        boolean bInheritHandle;
    };

}
