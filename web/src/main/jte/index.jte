@param lectio.web.DayView day
@param java.util.List<Integer> years
@param String feedWebcal
@param String umamiUrl
@param String umamiWebsiteId
@template.layout(
  title = day.name() + " – Lektionarium",
  years = years,
  feedWebcal = feedWebcal,
  umamiUrl = umamiUrl,
  umamiWebsiteId = umamiWebsiteId,
  content = @`
    @template.dagkort(day = day)
  `)
