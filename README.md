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
{Title} Encuesta de Satisfaccion sobre algun servicio

{Question} A que Direccion de Staff Reporta? 
{Config optional => true}
{Config labels => true}
-> ITD
-> P &amp; A
-> Ingenieria
-> FSC
-> HR
-> Marketing


{Question} Que intereses tienes de crecimiento
[] Informatica
[] Operaciones Aduana
[] Operaciones Domestico
[] Retails


{Question} Considera usted que los empleados de IT en este proyecto tienen buena disposicion ante sus requerimientos?
{Config axis => Disposicion}
() Totalmente en desacuerdo
() Parcialmente en desacuerdo
() Neutral
() Parcialmente de acuerdo
() Totalmente de acuerdo

{Question} Las solicitudes presentadas por usted son atendidas y solucionadas de forma oportuna?
{Config axis => Atencion}
() Totalmente en desacuerdo
() Parcialmente en desacuerdo
() Neutral
() Parcialmente de acuerdo
() Totalmente de acuerdo

{Question} En terminos de calidad sus requerimientos son atendidos satisfactoriamente?
{Config axis => Calidad}
() Totalmente en desacuerdo
() Parcialmente en desacuerdo
() Neutral
() Parcialmente de acuerdo
() Totalmente de acuerdo

{Question} Ve necesario incrementar los recursos del area que lo atendio?
{Config axis => Recursos}
() Totalmente en desacuerdo
() Parcialmente en desacuerdo
() Neutral
() Parcialmente de acuerdo
() Totalmente de acuerdo

{Question} Por que?
____________________

{Question} Como valoraria el area que esta evaluando?
{Config axis => General}
() Fuerte necesidad de mejora
() Alguna necesidad de mejora
() Neutral
() Cumplio con mis expectativas
() Excedio mis expectativas

{Question} Por favor escriba una sugerencia de como podemos mejorar para otros proyectos?
____________________

{Question} Cumplio su expectativa inicial?
() Si
() No
() Neutral


{EmailSubject} Encuenta de satisfaccion
{EmailTitle} Valoramos mucho tu opinion
{EmailContent} Estimado usuario

Recibes este correo porque recientemente te ayudamos a prender tu computadora y como parte de nuestro compromiso por ofrecer un mejor servicio, te pedimos que nos ayudes contestando una breve encuenta.

Puedes acceder a la encuestas desde este link:

<<Contestar Encuesta>>

Agradecemos mucho su tiempo.

Atte.

{EmailSign} Cultome Corp.

{SendTo one@example.com, two@example.com}
```