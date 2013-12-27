package com.izgoy.txttr;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class SmsHandlerService extends IntentService {

    public static final String TAG = "SmsHandlerService";

    public static final String ACTION_HANDLE_SMS = "com.izgoy.txttr.action.HANDLE_SMS";

    public static final String EXTRA_ADDRESS = "com.izgoy.txttr.extra.ADDRESS";
    public static final String EXTRA_BODY = "com.izgoy.txttr.extra.BODY";

    private UserManager userManager;

    public static void startHandleSMS(Context context, String address, String body) {
        Intent intent = new Intent(context, SmsHandlerService.class);
        intent.setAction(ACTION_HANDLE_SMS);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_BODY, body);
        context.startService(intent);
    }

    public SmsHandlerService() {
        super("SmsHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userManager = new UserManager(this);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_HANDLE_SMS.equals(action)) {
                final String address = intent.getStringExtra(EXTRA_ADDRESS);
                final String body = intent.getStringExtra(EXTRA_BODY);
                handleSMS(address, body);
            }
        }
    }

    private void handleSMS(String address, String body) {
        Log.v(TAG, address + " >>> " + body);
        String[] args = body.split(" ");
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("on")) {
                handleCommandOn(address, args);
                return;
            }
            if (command.equals("list")) {
                handleCommandList(address, args);
                return;
            }
            if (command.equals("off")) {
                handleCommandOff(address, args);
                return;
            }
        }
        if (userManager.isAddressRegistered(address)) {
            String nick = userManager.getAll().get(address);
            String body2 = String.format("%s: %s", nick.toUpperCase(), body);
            userManager.sendTextMessageToAll(body2, address);
            userManager.sendTextMessage(address, body2);
        }
    }

    private void handleCommandOn(String address, String[] args) {

        if (args.length < 2) {
            userManager.sendTextMessage(address, getString(R.string.message_error_nick_required));
            return;
        }

        String nick = args[1].toUpperCase();

        if (userManager.isNickRegistered(nick)) {
            userManager.sendTextMessage(address, getString(R.string.message_error_nick_taken));
            return;
        }

        if (nick.length() < TxttrApplication.MIN_NICK_LENGTH) {
            userManager.sendTextMessage(address, getString(R.string.message_error_nick_too_short, TxttrApplication.MIN_NICK_LENGTH));
            return;
        }

        if (nick.length() > TxttrApplication.MAX_NICK_LENGTH) {
            userManager.sendTextMessage(address, getString(R.string.message_error_nick_too_long, TxttrApplication.MAX_NICK_LENGTH));
            return;
        }

        userManager.register(address, nick);
        userManager.sendTextMessage(address, getString(R.string.message_success_registration));
        userManager.sendTextMessageToAll(getString(R.string.message_success_registration_all, nick), address);
    }

    private void handleCommandOff(String address, String[] args) {
        String nick = userManager.getNick(address);
        userManager.unregister(address);
        userManager.sendTextMessage(address, getString(R.string.message_success_unregistration));
        if (nick != null) {
            userManager.sendTextMessageToAll(getString(R.string.message_success_unregistration_all, nick), address);
        }
    }

    private void handleCommandList(String address, String[] args) {
        String reply = getString(R.string.message_registered_users,
                TextUtils.join(", ", userManager.getAll().values()));
        userManager.sendTextMessage(address, reply);
    }

}
