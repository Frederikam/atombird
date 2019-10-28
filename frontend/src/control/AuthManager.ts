import axios from "axios";
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
            callback(LOGGED_OUT_STATUS);
            return;
        }

        axios.post(globals.accountStatusUrl, null, {
            headers: {Authorization: token}
        }).then(res => {
            this.cachedStatus = new AccountStatus(true, res.data.email);
            callback(this.cachedStatus);
        }).catch(error => {
            console.log(error);
            if (error.response && error.response.status === 403) {
                localStorage.removeItem("token");
                this.cachedStatus = null;
                console.log("Removed token due to error 403");
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
}

export default new AuthManager()