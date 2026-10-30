package free.taro.mak.call

object Data {
    private val ruMap = HashMap<String, List<String>>()
    private val enMap = HashMap<String, List<String>>()

    private fun m(k: String, ru: String, en: String) {
        ruMap[k] = ru.split(";")
        enMap[k] = en.split(";")
    }

    fun meanings(c: Card, ru: Boolean): List<String> {
        val map = if (ru) ruMap else enMap
        return map[c.key] ?: listOf("?")
    }

    init {
        m("2H", "взаимная симпатия;романтическое знакомство;примирение;начало близких отношений", "mutual sympathy;a romantic meeting;reconciliation;the start of a close bond")
        m("2D", "финансовое соглашение;денежное предложение;совместная покупка;выгодное сотрудничество", "a financial agreement;a money offer;a shared purchase;a profitable partnership")
        m("2C", "совместная работа;новое знакомство через дело;командная работа;взаимная помощь на пути к цели", "working together;a new contact through activity;teamwork;mutual help toward a goal")
        m("2S", "сложный выбор;разногласия;неуверенность между двумя вариантами;необходимость принять решение", "a hard choice;disagreement;doubt between two options;a decision you can't avoid")
        m("3H", "ревность в окружении;поверхностные друзья;эмоции, которые мешают здраво мыслить;лишние слухи вокруг отношений", "jealousy around you;shallow friends;emotions clouding clear thinking;gossip around a relationship")
        m("3D", "обман в мелочах;упущенная выгода;ложные обещания о деньгах;трата сил ради небольшой прибыли", "deceit in small things;a missed gain;false promises about money;spending effort for a tiny profit")
        m("3C", "пустая трата сил;идея, которая не взлетает;провал из-за спешки;критика, которая бьёт по самолюбию", "wasted effort;an idea that won't take off;failure from rushing;criticism that stings your pride")
        m("3S", "недопонимание;лишние переживания;сложности в общении;влияние посторонних людей", "a misunderstanding;needless worry;trouble communicating;outsiders meddling")
        m("4H", "домашнее напряжение;застой в отношениях;близкие, которые давят заботой;чувство, что вы живёте по привычке", "tension at home;a relationship gone stale;loved ones smothering you with care;the feeling of living on habit")
        m("4D", "жадность или скупость;деньги, застрявшие без движения;страх потерять накопленное;мелкие долги и обязательства", "greed or stinginess;money stuck without moving;fear of losing your savings;small debts and obligations")
        m("4C", "тяжёлая рутина;выгорание от однообразной работы;застой в делах;обязанности, которые давят", "heavy routine;burnout from monotonous work;stalled affairs;duties that weigh on you")
        m("4S", "застой;отсутствие перемен;временное ограничение;необходимость сделать паузу", "stagnation;no change;a temporary limit;a need to pause")
        m("5H", "эмоциональные перемены;ревность;неожиданный разговор;изменение отношений", "emotional shifts;jealousy;an unexpected talk;relationships changing")
        m("5D", "неожиданные расходы;денежные колебания;незапланированная покупка;внезапная финансовая возможность", "unexpected expenses;money swings;an unplanned purchase;a sudden financial chance")
        m("5C", "конкуренция;смена планов;большая нагрузка;необходимость приложить больше усилий", "competition;changed plans;a heavy load;the need for extra effort")
        m("5S", "конфликт;серьёзное разногласие;сбой привычного порядка;трудный период", "conflict;a serious clash;routine falling apart;a rough period")
        m("6H", "примирение;восстановление отношений;поддержка близкого человека;приятная встреча или воспоминание", "reconciliation;mending a relationship;support from someone close;a warm memory or meeting")
        m("6D", "подарок;небольшая прибыль;денежная помощь;удачная покупка", "a gift;a small gain;financial help;a good purchase")
        m("6C", "успех благодаря стараниям;продвижение в работе или учёбе;полезная поездка;постепенное достижение цели", "success through effort;progress at work or study;a useful trip;slowly reaching the goal")
        m("6S", "выход из сложной ситуации;отдаление от проблемы;смена обстановки;постепенное улучшение", "a way out of trouble;distance from the problem;a change of scene;slow improvement")
        m("7H", "сомнения в чувствах;неопределённость в отношениях;ожидание признания;необходимость понять свои эмоции", "doubts about feelings;uncertainty in a relationship;waiting for a confession;the need to understand your own emotions")
        m("7D", "ожидание денег или новости;проверка предложения;документы, которые нужно изучить внимательно;неясность в финансовом вопросе", "waiting for money or news;an offer to be checked;documents to read carefully;a murky money matter")
        m("7C", "испытание в учёбе или работе;проверка навыков;необходимость терпения;сложная, но решаемая задача", "a test at work or study;your skills being checked;a call for patience;a hard but solvable task")
        m("7S", "скрытая информация;недоверие;необходимость разобраться в обстоятельствах;сомнения и внутренние переживания", "hidden information;distrust;a need to sort out the circumstances;doubt and inner turmoil")
        m("8H", "важный разговор о чувствах;приглашение;приятное сообщение;сближение с человеком", "an important talk about feelings;an invitation;a pleasant message;getting closer to someone")
        m("8D", "новость о деньгах;финансовое предложение;получение документов;сведения о покупке или сделке", "news about money;a financial offer;documents arriving;details of a deal")
        m("8C", "активная деятельность;много работы или учёбы;новые задачи;поездка или деловая встреча", "busy activity;lots of work or study;new tasks;a trip or business meeting")
        m("8S", "серьёзный разговор;необходимость прояснить ситуацию;важное решение;развеянное недоразумение", "a serious conversation;clearing things up;an important decision;a misunderstanding exposed")
        m("9H", "исполнение желания;счастье в отношениях;эмоциональное удовлетворение;долгожданная встреча", "a wish coming true;happiness in love;emotional fulfilment;a long awaited meeting")
        m("9D", "финансовая удача;награда;выгодная возможность;материальный успех", "financial luck;a reward;a profitable chance;material success")
        m("9C", "достижение цели;успех в проекте;признание заслуг;хороший результат после стараний", "reaching the goal;project success;recognition;a good result after hard work")
        m("9S", "сильные переживания перед результатом;осознание ошибки;конец тяжёлого периода;освобождение от старых сомнений", "strong nerves before the result;realising a mistake;the end of a heavy period;release from old doubts")
        m("TH", "большое счастье;семейное торжество;радостное событие;исполнение важного желания", "great happiness;a family celebration;a joyful event;an important wish fulfilled")
        m("TD", "крупная финансовая возможность;успешная сделка;большая покупка;материальный результат", "a big financial chance;a successful deal;a major purchase;a material result")
        m("TC", "успешное завершение проекта;большое достижение;завершение обучения или работы;переход на новый уровень", "a project finished well;a big achievement;study or work completed;a move to a new level")
        m("TS", "окончательное решение;завершение этапа;отказ от старого;необходимость идти дальше", "a final decision;the end of a stage;letting go of the old;time to move on")
        m("JH", "ревнивый молодой человек;легкомысленное обещание;флирт без серьёзных намерений;обида из-за мелочи", "a jealous young man;a careless promise;flirting with no serious intent;hurt over a trifle")
        m("JD", "ненадёжная новость о деньгах;человек, который тянет к лишним тратам;обещание без исполнения;мелкий обман в сделке", "unreliable news about money;someone pulling you into needless spending;a promise left unkept;petty deceit in a deal")
        m("JC", "ленивый или безответственный помощник;подвох в совместном деле;срыв договорённостей;несерьёзный подход к работе", "a lazy or irresponsible helper;a catch in a joint venture;broken agreements;an unserious attitude to work")
        m("JS", "неприятное сообщение;спор или резкие слова;неожиданное предупреждение;человек, который может создать трудности", "an unpleasant message;an argument or sharp words;a sudden warning;someone who may cause trouble")
        m("QH", "заботливая женщина;любящий и близкий человек;эмоциональная поддержка;искренность и доброта", "a caring woman;a loving close person;emotional support;sincerity and kindness")
        m("QD", "расчётливая и холодная женщина;зависть из-за денег;корыстный интерес;сплетни вокруг финансов", "a calculating cold woman;envy over money;self-serving interest;gossip about finances")
        m("QC", "завистливая подруга или коллега;подковёрная борьба;критика за спиной;женщина, которая мешает делу", "an envious friend or colleague;behind the scenes rivalry;criticism behind your back;a woman who gets in the way")
        m("QS", "строгая женщина;холодность в общении;скрытность;необходимость установить личные границы", "a stern woman;coldness in communication;secrecy;the need to set personal boundaries")
        m("KH", "властный и ревнивый мужчина;эмоциональное давление;обман доверия;холодность вместо поддержки", "a possessive jealous man;emotional pressure;broken trust;coldness instead of support")
        m("KD", "деловой мужчина;финансовый авторитет;успешный предприниматель;важное предложение или помощь", "a businessman;a financial authority;a successful entrepreneur;an important offer or help")
        m("KC", "руководитель;наставник;влиятельный человек;успех благодаря связям и опыту", "a leader;a mentor;an influential person;success through connections and experience")
        m("KS", "строгий руководитель;властный человек;серьёзное решение;авторитетная фигура, от которой многое зависит", "a strict boss;a domineering person;a serious decision;an authority figure many things depend on")
        m("AH", "новая любовь;сильные чувства;начало счастливых отношений;радостное событие в семье", "new love;strong feelings;the start of a happy relationship;a joyful family event")
        m("AD", "важное письмо или сообщение;новая возможность;получение документов;важное предложение", "an important letter or message;a new opportunity;documents arriving;an important offer")
        m("AC", "начало большого проекта;новая работа или учёба;перспективная идея;шанс добиться успеха", "the start of a big project;a new job or course;a promising idea;a chance to succeed")
        m("AS", "освобождение от старого, что тянуло назад;удачный разрыв с тем, что мешало;поворот к лучшему;сильное и верное решение;новая глава, которую ты выбираешь сам;честность, которая приносит облегчение", "freedom from the old weight;a lucky break with what held you back;a turn for the better;a strong and right decision;a new chapter you choose yourself;honesty that brings relief")
        m("JK", "неожиданный поворот событий;случайность;непредсказуемый результат;необычная возможность;ситуация, которую нельзя было заранее учесть;нарушение привычного хода событий", "an unexpected twist;pure chance;an unpredictable result;an unusual opportunity;a situation nobody could foresee;the usual course of events breaking")
    }
}
