Feedback
==============

Usage
====

```{Title}``` Declares the poll title

```{Question}``` Begins a question declaration. After this line should go the response options. There are 4 types of response options:

  * ```()``` Radio Buttons. Can only choose one option.
  * ```[]``` Checkboxes. Can choose one or more options. (Check ```Config``` tag).
  * ```->``` Combo Box. Can choose only one option.
  * ```___``` Textarea. Free text area.

```{Config}``` Add a configuration to a question. At this moment only one attribute is valid:

  * ```choose => n``` Apply to Checkboxes only. Add a validation where the user can only choose, at most, n options.

```{SendTo}``` Declares the sender list. The emails should be separed by commas.

```{EmailSubject}``` Declares the E-Mail subject to use when send the email to the users.

```{EmailTitle}``` Declares the table title inside the E-Mail. The default is **"In IT we appreciate your feedback"**

```{EmailContent}``` Declares the text inside the E-Mail's table. This content should include a special tag, that creates a button with the link to the poll, in any part of the content, as follows:

```<<Text for the link>>```

If not specified, the default is:

```
You are receiving this mail because we provided you service recently and as part 
of our efforts to improve our service, we would like you to help us answering a brief poll about it.

You can access the poll in the following link

<<TAKE ME TO THE POLL!>>

We want to thank you in advance for your time.

Regards,
```

```{EmailSign}``` Declares the sign for the E-Mail. Its just a styled text in the bottom of the E-Mail body. The default is **"LAC IT Department"**

Example
=======
```

```