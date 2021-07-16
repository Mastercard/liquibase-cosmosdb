package liquibase.nosql.changelog;

import liquibase.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Objects.isNull;

public abstract class AbstractNoSqlItemToDocumentConverter<I, D>  {

    public static final String ISO_8601_UTC_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public final SimpleDateFormat dateFormatter = new SimpleDateFormat(ISO_8601_UTC_DATETIME_FORMAT);

    public abstract D toDocument(I item);

    public abstract I fromDocument(D document);

    public Date toDate(final String dateString) {
        try {
            if (isNull(StringUtil.trimToNull(dateString))) {
                return null;
            }
            return dateFormatter.parse(dateString);
        } catch (final Exception e) {
            throw new IllegalArgumentException ("Cannot parse to Date: [" + dateString + "] with pattern: " + ISO_8601_UTC_DATETIME_FORMAT, e);
        }
    }

    public String fromDate(final Date date) {
        if (isNull(date)) {
            return null;
        }
        return dateFormatter.format(date);
    }

}
