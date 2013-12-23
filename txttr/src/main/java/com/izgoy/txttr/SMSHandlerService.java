package com.izgoy.txttr;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Map;

public class SMSHandlerService extends IntentService {

    public static final String ACTION_HANDLE_SMS = "com.izgoy.txttr.action.HANDLE_SMS";

    public static final String EXTRA_ADDRESS = "com.izgoy.txttr.extra.ADDRESS";
    public static final String EXTRA_BODY = "com.izgoy.txttr.extra.BODY";

    private UserManager userManager;
    private SmsManager smsManager;

    public static void startHandleSMS(Context context, String address, String body) {
        Intent intent = new Intent(context, SMSHandlerService.class);
        intent.setAction(ACTION_HANDLE_SMS);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_BODY, body);
        context.startService(intent);
    }

    public SMSHandlerService() {
        super("SMSHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userManager = new UserManager(this);
        smsManager = SmsManager.getDefault();
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
        String[] args = body.split(" ");
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("on")) {
                handleCommandOn(address, args);
                return;
            }
            /*if (command.equals("off")) {
                handleCommandOff(address, args);
                return;
            }*/
        }
        if (userManager.isAddressRegistered(address)) {
            String nick = userManager.getAll().get(address);
            String body2 = String.format("%s: %s", nick.toUpperCase(), body);
            ArrayList<String> parts = smsManager.divideMessage(body2);
            for (Map.Entry<String, String> user : userManager.getAll().entrySet()) {
                smsManager.sendMultipartTextMessage(user.getKey(), null, parts, null, null);
            }
        }
    }

    private void handleCommandOn(String address, String[] args) {

        if (args.length < 2) {
            smsManager.sendTextMessage(address, null,
                    getString(R.string.message_error_nick_required), null, null);
            return;
        }

        String nick = args[1].toUpperCase();

        if (userManager.isNickRegistered(nick)) {
            smsManager.sendTextMessage(address, null,
                    getString(R.string.message_error_nick_taken), null, null);
            return;
        }

        if (nick.length() < TxttrApplication.MIN_NICK_LENGTH) {
            smsManager.sendTextMessage(address, null,
                    getString(R.string.message_error_nick_too_short, TxttrApplication.MIN_NICK_LENGTH), null, null);
            return;
        }

        if (nick.length() > TxttrApplication.MAX_NICK_LENGTH) {
            smsManager.sendTextMessage(address, null,
                    getString(R.string.message_error_nick_too_long, TxttrApplication.MAX_NICK_LENGTH), null, null);
            return;
        }

        userManager.register(address, nick);
        smsManager.sendTextMessage(address, null,
                getString(R.string.message_success_registration), null, null);

    }

    private void handleCommandOff(String address, String[] args) {
        userManager.unregister(address);
        smsManager.sendTextMessage(address, null,
                getString(R.string.message_success_unregistration), null, null);
    }

}
