import api from "./Api";
import globals from "../globals";

class AccountStatus {
    readonly isLoggedIn: boolean;
    readonly email: string | null;

    constructor(isLoggedIn: boolean, email: string | null) {
        this.isLoggedIn = isLoggedIn;
        this.email = email;
    }
}

const LOGGED_OUT_STATUS = new AccountStatus(false, null);

class AuthManager {
    private cachedStatus: AccountStatus | null = null;
    private statusKnown = false;

    /**
     * Determines the account status and returns it via the given callback.
     * If the callback receives null it means that the check failed, and that we may still be logged in.
     */
    getStatus(callback: (status: AccountStatus | null) => void) {
        if (this.cachedStatus && this.cachedStatus.isLoggedIn) {
            callback(this.cachedStatus);
            return;
        }

        let token = localStorage.getItem("token");

        if (token == null) {
            this.statusKnown = true;
            callback(LOGGED_OUT_STATUS);
            return;
        }

        api.getStatus().then(res => {
            this.cachedStatus = new AccountStatus(true, res.data.email);
            this.statusKnown = true;
            console.log("Auth token is still valid. Logged in as", this.cachedStatus.email);
            callback(this.cachedStatus);
        }).catch(error => {
            console.log(error);
            if (error.response && error.response.status === 403) {
                localStorage.removeItem("token");
                console.log("Removed token due to error 403");
                this.cachedStatus = null;
                this.statusKnown = true;
                callback(LOGGED_OUT_STATUS);
                return
            }
            callback(null)
        })
    }

    /**
     * Returns AccountStatus if we know it, or assume we are logged out.
     */
    getStatusIfKnown(): AccountStatus {
        return this.cachedStatus ? this.cachedStatus : LOGGED_OUT_STATUS;
    }

    isStatusKnown() {
        return this.statusKnown;
    }

    /** For when we are granted a new token */
    provideToken(token: string) {
        console.log("Received auth token. Checking status...");
        localStorage.setItem("token", token);
        this.getStatus((status: AccountStatus | null) => {
            if (status && status.isLoggedIn) globals.router.forceNavigate();
        });
    }

    logOut() {
        console.log("Logged out");
        localStorage.removeItem("token");
        this.cachedStatus = LOGGED_OUT_STATUS;
        this.statusKnown = true;
        globals.router.navigate("/login");
    }
}

export default new AuthManager()
export { AccountStatus }
