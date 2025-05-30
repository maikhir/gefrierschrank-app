export const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

export const formatDateWithTime = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

export const getDaysUntilExpiration = (expirationDate: string): number => {
  const now = new Date()
  const expiry = new Date(expirationDate)
  const diffTime = expiry.getTime() - now.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

export const getExpirationStatus = (expirationDate: string | null): 'fresh' | 'expiring' | 'critical' | 'expired' => {
  if (!expirationDate) return 'fresh'
  
  const daysUntil = getDaysUntilExpiration(expirationDate)
  
  if (daysUntil < 0) return 'expired'
  if (daysUntil <= 1) return 'critical'
  if (daysUntil <= 7) return 'expiring'
  return 'fresh'
}

export const getExpirationStatusText = (expirationDate: string | null): string => {
  if (!expirationDate) return 'Unbegrenzt haltbar'
  
  const daysUntil = getDaysUntilExpiration(expirationDate)
  
  if (daysUntil < 0) return `Abgelaufen (${Math.abs(daysUntil)} Tage)`
  if (daysUntil === 0) return 'Läuft heute ab'
  if (daysUntil === 1) return 'Läuft morgen ab'
  if (daysUntil <= 7) return `Läuft in ${daysUntil} Tagen ab`
  return `Haltbar für ${daysUntil} Tage`
}

export const getRelativeDate = (dateString: string): string => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) return 'Heute'
  if (diffDays === 1) return 'Gestern'
  if (diffDays < 7) return `Vor ${diffDays} Tagen`
  if (diffDays < 30) return `Vor ${Math.floor(diffDays / 7)} Wochen`
  if (diffDays < 365) return `Vor ${Math.floor(diffDays / 30)} Monaten`
  return `Vor ${Math.floor(diffDays / 365)} Jahren`
}

export const isValidDate = (dateString: string): boolean => {
  const date = new Date(dateString)
  return !isNaN(date.getTime())
}

export const getISODateString = (date: Date = new Date()): string => {
  return date.toISOString().split('T')[0]
}